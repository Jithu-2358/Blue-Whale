// mapper/CommandMapper.java
package com.bluewhale.mapper;

import com.bluewhale.dto.CommandRequest;
import com.bluewhale.dto.CommandResponse;
import com.bluewhale.model.Command;
import java.time.Instant;

public class CommandMapper {

    public static Command toEntity(CommandRequest req) {
        Command c = new Command();
        c.setDeviceId(req.getDeviceId());
        c.setCommand(req.getCommand());
        c.setParams(req.getParams());
        c.setIssuedAt(Instant.now());
        c.setStatus("pending");
        return c;
    }

    public static CommandResponse toResponse(Command c) {
        CommandResponse r = new CommandResponse();
        r.setId(c.getId());
        r.setDeviceId(c.getDeviceId());
        r.setCommand(c.getCommand());
        r.setParams(c.getParams());
        r.setIssuedAt(c.getIssuedAt());
        r.setStatus(c.getStatus());
        r.setResult(c.getResult());
        return r;
    }
}