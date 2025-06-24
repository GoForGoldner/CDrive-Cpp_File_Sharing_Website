package com.goforgoldner.c_drive.controller;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.repositories.CppFileRepository;
import com.goforgoldner.c_drive.service.CppCompilerService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CppWebSocketController {

  private final CppFileRepository cppFileRepository;
  private final CppCompilerService cppCompilerService;
  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public CppWebSocketController(
      CppFileRepository cppFileRepository,
      CppCompilerService cppCompilerService,
      SimpMessagingTemplate messagingTemplate) {
    this.cppFileRepository = cppFileRepository;
    this.cppCompilerService = cppCompilerService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/execute-cpp")
  public void handleCppExecution(
      @Payload String fileIdString, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    String translatedCompilerOutputDestination = "/queue/compiler-output-user" + sessionId;
    String translatedErrorDestination = "/queue/errors" + sessionId;

    System.out.println("WS DEBUG: Message received on /execute-cpp endpoint");

    headerAccessor
        .getMessageHeaders()
        .forEach((key, value) -> System.out.println("    " + key + ": " + value));

    Optional<CppFileEntity> cppFile = cppFileRepository.findById(Long.parseLong(fileIdString));

    if (cppFile.isEmpty()) {
      System.out.println("WS DEBUG: Sending error message to user session: " + sessionId);
      messagingTemplate.convertAndSend(translatedErrorDestination, "File Not Found.");
      return;
    }

    System.out.println("WS DEBUG: About to call executeFile with sessionId: " + sessionId);

    cppCompilerService.executeFile(cppFile.get(), sessionId, messagingTemplate);

    System.out.println("WS DEBUG: WebSocket handler complete for session: " + sessionId);
  }

  @MessageMapping("/cpp-input")
  public void handleCppInputs(@Payload String line, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    System.out.println("WS DEBUG: Input received: " + line);

    cppCompilerService.sendMessageToTerminal(sessionId, line);
  }
}
