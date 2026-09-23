// service/CommandService.java
package com.bluewhale.service;

import com.bluewhale.dto.CommandRequest;
import com.bluewhale.exception.DeviceNotFoundException;
import com.bluewhale.mapper.CommandMapper;
import com.bluewhale.model.Command;
import com.bluewhale.repository.CommandRepository;
import com.bluewhale.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class CommandService {

    private final CommandRepository commandRepository;
    private final DeviceRepository deviceRepository;

    public CommandService(CommandRepository commandRepository, DeviceRepository deviceRepository) {
        this.commandRepository = commandRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public Command issue(CommandRequest req) {
        if (!deviceRepository.existsById(req.getDeviceId())) {
            throw new DeviceNotFoundException(req.getDeviceId());
        }
        Command c = CommandMapper.toEntity(req);
        return commandRepository.save(c);
    }

    @Transactional
    public List<Command> poll(String deviceId) {
        List<Command> pending = commandRepository
                .findByDeviceIdAndStatusOrderByIdAsc(deviceId, "pending");
        List<Command> toDeliver = pending.stream().limit(5).toList();
        toDeliver.forEach(c -> {
            c.setStatus("delivered");
            commandRepository.save(c);
        });
        return toDeliver;
    }

    @Transactional
    public void complete(Long commandId, String result, String status) {
        commandRepository.findById(commandId).ifPresent(c -> {
            c.setResult(result);
            c.setStatus(status);
            commandRepository.save(c);
        });
    }

    public List<Command> history(String deviceId) {
        return commandRepository.findByDeviceIdOrderByIssuedAtDesc(deviceId);
    }
}