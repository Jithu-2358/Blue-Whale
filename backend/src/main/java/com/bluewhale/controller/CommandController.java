// controller/CommandController.java
package com.bluewhale.controller;

import com.bluewhale.dto.CommandRequest;
import com.bluewhale.dto.CommandResponse;
import com.bluewhale.mapper.CommandMapper;
import com.bluewhale.model.Command;
import com.bluewhale.service.CommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    /** Admin issues command — POST /api/v1/command */
    @PostMapping("/command")
    public ResponseEntity<?> issue(@Valid @RequestBody CommandRequest req) {
        Command c = commandService.issue(req);
        return ResponseEntity.ok(Map.of("status", "issued", "commandId", c.getId()));
    }

    /** Device polls pending commands — POST /api/v1/poll */
    @PostMapping("/poll")
    public ResponseEntity<?> poll(@RequestBody Map<String, String> body) {
        String deviceId = body.get("deviceId");
        if (deviceId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "deviceId required"));
        }
        List<Command> cmds = commandService.poll(deviceId);
        List<Map<String, Object>> list = cmds.stream().map(c -> Map.<String, Object>of(
                "id", c.getId(),
                "command", c.getCommand(),
                "params", c.getParams() != null ? c.getParams() : "{}"
        )).toList();
        return ResponseEntity.ok(Map.of("commands", list));
    }

    /** Device submits result — POST /api/v1/result */
    @PostMapping("/result")
    public ResponseEntity<?> result(@RequestBody Map<String, Object> body) {
        Long cmdId = Long.valueOf(body.get("commandId").toString());
        String result = (String) body.getOrDefault("result", "");
        String status = (String) body.getOrDefault("status", "completed");
        commandService.complete(cmdId, result, status);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/commands/{deviceId}")
    public ResponseEntity<List<CommandResponse>> history(@PathVariable String deviceId) {
        return ResponseEntity.ok(commandService.history(deviceId).stream()
                .map(CommandMapper::toResponse).toList());
    }
}