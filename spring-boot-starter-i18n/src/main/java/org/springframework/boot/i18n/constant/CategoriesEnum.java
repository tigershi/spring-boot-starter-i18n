package org.springframework.boot.i18n.constant;

public enum CategoriesEnum {
    DATES(1, "dates"),
    NUMBERS(2, "numbers"),
    PLURALS(4, "plurals"),
    MEASUREMENTS(8, "measurements"),
    CURRENCIES(16, "currencies"),
    DATE_FIELDS(32, "dateFields");

    private Integer type;

    private String text;

    CategoriesEnum(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public static CategoriesEnum getCategoriesEnumByText(String text){
        if ((text != null) && !text.equals("")){
            for(CategoriesEnum categoriesEnum : CategoriesEnum.values()){
                if(text.toUpperCase().equals(categoriesEnum.text.toUpperCase())){
                    return categoriesEnum;
                }
            }
        }
        return null;
    }

}
