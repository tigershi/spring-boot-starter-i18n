package org.springframework.boot.i18n.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.i18n.constant.CharConstant;
import org.springframework.boot.i18n.constant.L10nConstant;
import org.springframework.boot.i18n.constant.MsgConstant;
import org.springframework.boot.i18n.model.L10nException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;


public class L10nImageServiceImpl implements L10nImageService{
    private static Logger logger = LoggerFactory.getLogger(L10nImageServiceImpl.class);

    public FileChannel getCountryFlagChannel(String region, int scale, String imageType) throws L10nException {
        String result = null;
        switch (scale) {
            case 1:
                result = "1x1";
                break;
            case 2:
                result = "3x2";
                break;
            default:
                throw new L10nException(MsgConstant.IMAGE_NOT_SUPPORT_SCALE);
        }
        try {
            return getCountryFlagChannel(result, region.toUpperCase(), CharConstant.DOT + imageType);
        } catch (FileNotFoundException fe) {
            logger.warn(fe.getMessage(), fe);
            throw new L10nException(String.format(MsgConstant.IMAGE_NOT_SUPPORT_REGION, region), fe);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new L10nException(e.getMessage());
        }

    }


    private FileChannel getCountryFlagChannel(String scale, String shortName, String fileNameSuffix) throws Exception {

        StringBuilder sourcePath = new StringBuilder(L10nConstant.L10N_IMAGE_FLAG_BASE_PATH);
        sourcePath.append(scale.replaceAll("\\.", "").replaceAll("/", "")).append(File.separator);
        sourcePath.append(shortName.replaceAll("\\.", "").replaceAll("/", "")).append(fileNameSuffix);
        return new FileInputStream(new File(sourcePath.toString())).getChannel();

    }
}
