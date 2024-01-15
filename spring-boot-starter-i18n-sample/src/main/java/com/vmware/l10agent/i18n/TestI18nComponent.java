package com.vmware.l10agent.i18n;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.i18n.annotation.I18nComponent;

@I18nComponent("test")
@Setter
@Getter
public class TestI18nComponent {

    private String test_abc_msg="this is test abc message";
    private String test_bcd_msg="this is test bcd message";
    private String test_cde_msg="this is test cde message";
    private String test_def_msg="this is test def message";

}
