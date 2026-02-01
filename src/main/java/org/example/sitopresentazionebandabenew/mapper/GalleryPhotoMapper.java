package org.example.sitopresentazionebandabenew.mapper;

import org.example.sitopresentazionebandabenew.dto.requests.GalleryPhotoRequest;
import org.example.sitopresentazionebandabenew.dto.responses.GalleryPhotoResponse;
import org.example.sitopresentazionebandabenew.entity.GalleryPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GalleryPhotoMapper {

    GalleryPhotoResponse toResponse(GalleryPhoto photo);

    List<GalleryPhotoResponse> toResponseList(List<GalleryPhoto> photos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "src", ignore = true)
    @Mapping(target = "thumbnailSrc", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    GalleryPhoto toEntity(GalleryPhotoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "src", ignore = true)
    @Mapping(target = "thumbnailSrc", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromRequest(GalleryPhotoRequest request, @MappingTarget GalleryPhoto photo);
}
