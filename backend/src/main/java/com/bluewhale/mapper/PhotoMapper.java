// mapper/PhotoMapper.java
package com.bluewhale.mapper;

import com.bluewhale.dto.PhotoResponse;
import com.bluewhale.model.Photo;

public class PhotoMapper {

    public static PhotoResponse toResponse(Photo p) {
        PhotoResponse r = new PhotoResponse();
        r.setId(p.getId());
        r.setDeviceId(p.getDeviceId());
        r.setPhotoUrl("/photos/" + p.getFilePath().substring(p.getFilePath().lastIndexOf('/') + 1));
        r.setFileSize(p.getFileSize());
        r.setCapturedAt(p.getCapturedAt());
        r.setCamera(p.getCamera());
        return r;
    }
}