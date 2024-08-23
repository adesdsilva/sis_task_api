package setecolinas.com.sis_task_manager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import setecolinas.com.sis_task_manager.controller.SubTaskController;
import setecolinas.com.sis_task_manager.dto.SubTaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.SubTaskResponseDTO;
import setecolinas.com.sis_task_manager.service.SubTaskService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(SubTaskController.class)
public class SubTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubTaskService subTaskService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SubTaskController(subTaskService)).build();
    }

    @Test
    public void testCreateSubTask_Success() throws Exception {
        SubTaskRequestDTO requestDTO = new SubTaskRequestDTO("Subtarefa Teste", 1L);

        SubTaskResponseDTO responseDTO = new SubTaskResponseDTO(1L, "Subtarefa Teste", 1L);

        when(subTaskService.createSubTask(any(SubTaskRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/subtasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Subtarefa Teste"))
                .andExpect(jsonPath("$.taskId").value(1L));
    }

    @Test
    public void testGetSubTaskById_Success() throws Exception {
        SubTaskResponseDTO responseDTO = new SubTaskResponseDTO(1L, "Subtarefa Teste", 1L);

        when(subTaskService.getSubTaskById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/subtasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Subtarefa Teste"))
                .andExpect(jsonPath("$.taskId").value(1L));
    }
}

