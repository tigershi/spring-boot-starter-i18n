/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.springframework.boot.i18n.utils;


import org.springframework.boot.i18n.constant.CategoriesEnum;
import org.springframework.boot.i18n.constant.CharConstant;

import java.util.ArrayList;
import java.util.List;

public class CommonUtility {
  private CommonUtility() {}

    /**
     * Get categories by CategoriesEnum
     * @param scope
     * @param perfectMatch
     * If perfectMatchFlag = true and not found from the CategoriesEnum, return null
     * If perfectMatchFlag = false and not found from the CategoriesEnum，return the original value
     *
     * @return
     */
    public static List<String> getCategoriesByEnum(String scope, boolean perfectMatch){
        String[] categories = scope.split(CharConstant.COMMA);
        List<String> categoryList = new ArrayList<>();
        for (String cat : categories) {
            CategoriesEnum categoriesEnum = CategoriesEnum.getCategoriesEnumByText(cat);
            if (categoriesEnum == null) {
                if (perfectMatch) {
                    return null;
                }
                categoryList.add(cat);
            } else {
                categoryList.add(categoriesEnum.getText());
            }
        }
        return categoryList;
    }
}
