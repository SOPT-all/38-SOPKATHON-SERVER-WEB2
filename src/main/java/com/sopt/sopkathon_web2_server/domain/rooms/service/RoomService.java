package com.sopt.sopkathon_web2_server.domain.rooms.service;

import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.ParticipantRoleResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.SwapRoomRolesResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.VerifyInviteResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.repository.RoomRepository;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;

@Service
public class RoomService {

    private static final int INVITE_TOKEN_BYTE_LENGTH = 6;
    private static final int BROWSER_TOKEN_BYTE_LENGTH = 16;
    private static final String INVITE_PATH = "/invite/";
    private static final int CREATOR_ORDER = 1;
    private static final int GUEST_ORDER = 2;
    private static final int ROOM_PARTICIPANT_LIMIT = 2;

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final SecureRandom secureRandom;
    private final String inviteBaseUrl;

    public RoomService(
            RoomRepository roomRepository,
            ParticipantRepository participantRepository,
            @Value("${client.invite-base-url}") String inviteBaseUrl
    ) {
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
        this.secureRandom = new SecureRandom();
        this.inviteBaseUrl = removeTrailingSlash(inviteBaseUrl);
    }

    @Transactional
    public CreateRoomResponse createRoom() {
        InviteToken inviteToken = createUniqueInviteToken();
        Room room = roomRepository.save(new Room(inviteToken.hash()));
        BrowserToken browserToken = createUniqueBrowserToken();
        Participant participant = participantRepository.save(new Participant(
                room,
                CREATOR_ORDER,
                browserToken.hash(),
                ParticipantRole.CHILD
        ));

        return new CreateRoomResponse(
                room.getId(),
                inviteToken.value(),
                inviteBaseUrl + INVITE_PATH + inviteToken.value(),
                participant.getId(),
                participant.getRole(),
                browserToken.value()
        );
    }

    @Transactional
    public JoinRoomResponse joinRoom(String inviteToken) {
        JoinedParticipant joinedParticipant = joinParticipantWithInviteToken(inviteToken);
        Room room = joinedParticipant.room();
        Participant participant = joinedParticipant.participant();

        return new JoinRoomResponse(
                room.getId(),
                participant.getId(),
                participant.getRole(),
                joinedParticipant.browserToken().value()
        );
    }

    @Transactional
    public VerifyInviteResponse verifyInvite(String inviteToken) {
        JoinedParticipant joinedParticipant = joinParticipantWithInviteToken(inviteToken);
        Room room = joinedParticipant.room();
        Participant participant = joinedParticipant.participant();
        int joinedParticipantCount = Math.toIntExact(participantRepository.countByRoomId(room.getId()));

        return new VerifyInviteResponse(
                room.getId(),
                participant.getId(),
                participant.getParticipantOrder(),
                joinedParticipant.browserToken().value(),
                true,
                joinedParticipantCount
        );
    }

    @Transactional
    public SwapRoomRolesResponse swapRoles(Long roomId, String browserToken) {
        if (roomId == null || !StringUtils.hasText(browserToken)) {
            throw new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT);
        }

        String browserTokenHash = hashToken(browserToken);
        participantRepository.findByRoomIdAndBrowserTokenHash(roomId, browserTokenHash)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT));

        List<Participant> participants = participantRepository.findAllByRoomIdOrderByParticipantOrderAsc(roomId);
        if (!canSwapRoles(participants)) {
            throw new CustomException(ErrorCode.INVALID_ROLE_SWAP_REQUEST);
        }

        participants.forEach(participant -> {
            if (participant.getRole() == ParticipantRole.CHILD) {
                participant.updateRole(ParticipantRole.PARENT);
                return;
            }

            participant.updateRole(ParticipantRole.CHILD);
        });

        return new SwapRoomRolesResponse(
                roomId,
                participants.stream()
                        .map(participant -> new ParticipantRoleResponse(participant.getId(), participant.getRole()))
                        .toList()
        );
    }

    private InviteToken createUniqueInviteToken() {
        while (true) {
            String token = createInviteToken();
            String tokenHash = hashInviteToken(token);

            if (!roomRepository.existsByInviteTokenHash(tokenHash)) {
                return new InviteToken(token, tokenHash);
            }
        }
    }

    private JoinedParticipant joinParticipantWithInviteToken(String inviteToken) {
        Room room = findRoomByInviteToken(inviteToken);

        if (participantRepository.countByRoomId(room.getId()) >= ROOM_PARTICIPANT_LIMIT) {
            throw new CustomException(ErrorCode.ROOM_ALREADY_FULL);
        }

        BrowserToken browserToken = createUniqueBrowserToken();
        Participant participant = participantRepository.save(new Participant(
                room,
                GUEST_ORDER,
                browserToken.hash(),
                ParticipantRole.PARENT
        ));

        return new JoinedParticipant(room, participant, browserToken);
    }

    private Room findRoomByInviteToken(String inviteToken) {
        if (!StringUtils.hasText(inviteToken)) {
            throw new CustomException(ErrorCode.INVALID_INVITE_TOKEN);
        }

        String inviteTokenHash = hashToken(inviteToken);

        return roomRepository.findByInviteTokenHash(inviteTokenHash)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_TOKEN));
    }

    private String createInviteToken() {
        byte[] tokenBytes = new byte[INVITE_TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(tokenBytes);

        return HexFormat.of().formatHex(tokenBytes);
    }

    private String hashInviteToken(String inviteToken) {
        return hashToken(inviteToken);
    }

    private BrowserToken createUniqueBrowserToken() {
        while (true) {
            String token = createBrowserToken();
            String tokenHash = hashToken(token);

            if (!participantRepository.existsByBrowserTokenHash(tokenHash)) {
                return new BrowserToken(token, tokenHash);
            }
        }
    }

    private String createBrowserToken() {
        byte[] tokenBytes = new byte[BROWSER_TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(tokenBytes);

        return HexFormat.of().formatHex(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedToken = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashedToken);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }

    private boolean canSwapRoles(List<Participant> participants) {
        if (participants.size() != ROOM_PARTICIPANT_LIMIT) {
            return false;
        }

        long childCount = participants.stream()
                .filter(participant -> participant.getRole() == ParticipantRole.CHILD)
                .count();
        long parentCount = participants.stream()
                .filter(participant -> participant.getRole() == ParticipantRole.PARENT)
                .count();

        return childCount == 1 && parentCount == 1;
    }

    private record InviteToken(
            String value,
            String hash
    ) {
    }

    private record BrowserToken(
            String value,
            String hash
    ) {
    }

    private record JoinedParticipant(
            Room room,
            Participant participant,
            BrowserToken browserToken
    ) {
    }
}
