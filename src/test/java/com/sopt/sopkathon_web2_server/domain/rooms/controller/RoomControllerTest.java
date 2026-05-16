package com.sopt.sopkathon_web2_server.domain.rooms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createRoomReturnsCreatedRoomInviteInformation() throws Exception {
        mockMvc.perform(post("/api/rooms"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").isNumber())
                .andExpect(jsonPath("$.data.inviteToken").value(matchesPattern("[0-9a-f]{12}")))
                .andExpect(jsonPath("$.data.inviteUrl").value(matchesPattern("http://localhost:5173/invite/[0-9a-f]{12}")))
                .andExpect(jsonPath("$.data.participantId").isNumber())
                .andExpect(jsonPath("$.data.role").value("CHILD"))
                .andExpect(jsonPath("$.data.browserToken").value(matchesPattern("[0-9a-f]{32}")))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void joinRoomReturnsParentParticipant() throws Exception {
        String createResponse = mockMvc.perform(post("/api/rooms"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String inviteToken = createResponse.replaceAll(".*\"inviteToken\":\"([0-9a-f]{12})\".*", "$1");

        mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"" + inviteToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").isNumber())
                .andExpect(jsonPath("$.data.participantId").isNumber())
                .andExpect(jsonPath("$.data.role").value("PARENT"))
                .andExpect(jsonPath("$.data.browserToken").value(matchesPattern("[0-9a-f]{32}")))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void verifyInviteReturnsRoomAndBrowserToken() throws Exception {
        String createResponse = mockMvc.perform(post("/api/rooms"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String roomId = createResponse.replaceAll(".*\"roomId\":([0-9]+).*", "$1");
        String inviteToken = createResponse.replaceAll(".*\"inviteToken\":\"([0-9a-f]{12})\".*", "$1");

        mockMvc.perform(post("/api/invites/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"" + inviteToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").value(Integer.parseInt(roomId)))
                .andExpect(jsonPath("$.data.participantId").isNumber())
                .andExpect(jsonPath("$.data.participantOrder").value(2))
                .andExpect(jsonPath("$.data.browserToken").value(matchesPattern("[0-9a-f]{32}")))
                .andExpect(jsonPath("$.data.isNewParticipant").value(true))
                .andExpect(jsonPath("$.data.joinedParticipantCount").value(2))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void verifyInviteRejectsInvalidInviteToken() throws Exception {
        mockMvc.perform(post("/api/invites/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"invalid-token\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40002))
                .andExpect(jsonPath("$.error.message").value("초대 토큰이 올바르지 않습니다."));
    }

    @Test
    void verifyInviteRejectsFullRoom() throws Exception {
        String createResponse = mockMvc.perform(post("/api/rooms"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String inviteToken = createResponse.replaceAll(".*\"inviteToken\":\"([0-9a-f]{12})\".*", "$1");

        mockMvc.perform(post("/api/invites/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"" + inviteToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/invites/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"" + inviteToken + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40901))
                .andExpect(jsonPath("$.error.message").value("이미 참여자가 모두 입장한 방입니다."));
    }

    @Test
    void swapRoomRolesReturnsOneChildAndOneParent() throws Exception {
        String createResponse = mockMvc.perform(post("/api/rooms"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String roomId = createResponse.replaceAll(".*\"roomId\":([0-9]+).*", "$1");
        String inviteToken = createResponse.replaceAll(".*\"inviteToken\":\"([0-9a-f]{12})\".*", "$1");
        String browserToken = createResponse.replaceAll(".*\"browserToken\":\"([0-9a-f]{32})\".*", "$1");

        mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteToken\":\"" + inviteToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/rooms/" + roomId + "/roles/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"browserToken\":\"" + browserToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomId").value(Integer.parseInt(roomId)))
                .andExpect(jsonPath("$.data.participants.length()").value(2))
                .andExpect(jsonPath("$.data.participants[0].role").value("PARENT"))
                .andExpect(jsonPath("$.data.participants[1].role").value("CHILD"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }
}
