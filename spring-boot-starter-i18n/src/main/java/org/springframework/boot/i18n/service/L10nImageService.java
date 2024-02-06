package org.springframework.boot.i18n.service;

import org.springframework.boot.i18n.model.L10nException;

import java.nio.channels.FileChannel;

public interface L10nImageService {
    public FileChannel getCountryFlagChannel(String region, int scale, String imageType) throws L10nException;
}
