package com.sopt.sopkathon_web2_server.domain.rooms.service;

import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.SwapRoomRolesResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.VerifyInviteResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.repository.RoomRepository;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void createRoomCreatesRoomWithInviteTokenAndInviteUrl() {
        CreateRoomResponse response = roomService.createRoom();

        assertThat(response.roomId()).isNotNull();
        assertThat(response.inviteToken()).matches("[0-9a-f]{12}");
        assertThat(response.inviteUrl())
                .isEqualTo("http://localhost:5173/invite/" + response.inviteToken());
        assertThat(response.participantId()).isNotNull();
        assertThat(response.role()).isEqualTo(ParticipantRole.CHILD);
        assertThat(response.browserToken()).matches("[0-9a-f]{32}");
    }

    @Test
    void createRoomStoresInviteTokenHashInsteadOfPlainToken() {
        CreateRoomResponse response = roomService.createRoom();

        Room room = roomRepository.findById(response.roomId()).orElseThrow();

        assertThat(room.getInviteTokenHash())
                .isNotEqualTo(response.inviteToken())
                .matches("[0-9a-f]{64}");
        assertThat(roomRepository.existsByInviteTokenHash(room.getInviteTokenHash())).isTrue();
    }

    @Test
    void createRoomCreatesChildParticipant() {
        CreateRoomResponse response = roomService.createRoom();

        Participant participant = participantRepository.findById(response.participantId()).orElseThrow();

        assertThat(participant.getRoom().getId()).isEqualTo(response.roomId());
        assertThat(participant.getParticipantOrder()).isEqualTo(1);
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.CHILD);
        assertThat(participant.getBrowserTokenHash())
                .isNotEqualTo(response.browserToken())
                .matches("[0-9a-f]{64}");
    }

    @Test
    void joinRoomCreatesParentParticipant() {
        CreateRoomResponse createdRoom = roomService.createRoom();

        JoinRoomResponse response = roomService.joinRoom(createdRoom.inviteToken());

        assertThat(response.roomId()).isEqualTo(createdRoom.roomId());
        assertThat(response.participantId()).isNotNull();
        assertThat(response.role()).isEqualTo(ParticipantRole.PARENT);
        assertThat(response.browserToken()).matches("[0-9a-f]{32}");
    }

    @Test
    void verifyInviteCreatesParentParticipantWithJoinedCount() {
        CreateRoomResponse createdRoom = roomService.createRoom();

        VerifyInviteResponse response = roomService.verifyInvite(createdRoom.inviteToken());

        assertThat(response.roomId()).isEqualTo(createdRoom.roomId());
        assertThat(response.participantId()).isNotNull();
        assertThat(response.participantOrder()).isEqualTo(2);
        assertThat(response.browserToken()).matches("[0-9a-f]{32}");
        assertThat(response.isNewParticipant()).isTrue();
        assertThat(response.joinedParticipantCount()).isEqualTo(2);

        Participant participant = participantRepository.findById(response.participantId()).orElseThrow();

        assertThat(participant.getRoom().getId()).isEqualTo(createdRoom.roomId());
        assertThat(participant.getParticipantOrder()).isEqualTo(2);
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.PARENT);
        assertThat(participant.getBrowserTokenHash())
                .isNotEqualTo(response.browserToken())
                .matches("[0-9a-f]{64}");
    }

    @Test
    void swapRoomRolesKeepsExactlyOneChildAndOneParent() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        JoinRoomResponse joinedRoom = roomService.joinRoom(createdRoom.inviteToken());

        SwapRoomRolesResponse response = roomService.swapRoles(createdRoom.roomId(), createdRoom.browserToken());

        Participant creator = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        Participant guest = participantRepository.findById(joinedRoom.participantId()).orElseThrow();

        assertThat(creator.getRole()).isEqualTo(ParticipantRole.PARENT);
        assertThat(guest.getRole()).isEqualTo(ParticipantRole.CHILD);
        assertThat(response.participants())
                .extracting("role")
                .containsExactlyInAnyOrder(ParticipantRole.CHILD, ParticipantRole.PARENT);
    }

    @Test
    void joinRoomRejectsThirdParticipantToPreventDuplicateRoles() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        roomService.joinRoom(createdRoom.inviteToken());

        assertThatThrownBy(() -> roomService.joinRoom(createdRoom.inviteToken()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ROOM_ALREADY_FULL);
    }

    @Test
    void verifyInviteRejectsInvalidInviteToken() {
        assertThatThrownBy(() -> roomService.verifyInvite("invalid-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INVITE_TOKEN);
    }

    @Test
    void verifyInviteRejectsFullRoom() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        roomService.verifyInvite(createdRoom.inviteToken());

        assertThatThrownBy(() -> roomService.verifyInvite(createdRoom.inviteToken()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ROOM_ALREADY_FULL);
    }
}
