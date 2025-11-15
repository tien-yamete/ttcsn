package com.tien.sharedcontacts.media;

import java.util.List;

public record MultipleImageResponse (List<ImageUploadedEvent> uploadedEvents) {}