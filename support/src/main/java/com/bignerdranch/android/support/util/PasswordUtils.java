package com.bignerdranch.android.support.util;

import java.util.ArrayList;
import java.util.List;

public class PasswordUtils {

    private int mMinLength;
    private int mMaxLength;
    private List<ValidationType> mValidationTypes;

    public PasswordUtils() {
        mValidationTypes = new ArrayList<ValidationType>();
    }

    public PasswordUtils addValidationType(ValidationType validationType) {
        mValidationTypes.add(validationType);
        return this;
    }

    public PasswordUtils validateLength(int minLength, int maxLength) {
        mMinLength = minLength;
        mMaxLength = maxLength;
        return this;
    }

    public ValidationResultType validate(String password) {

        for (ValidationType validationType : mValidationTypes) {
            switch (validationType) {
                case LENGTH:
                    if (invalidLength(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_LOWERCASE:
                    if (!containsLowercase(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_UPPERCASE:
                    if (!containsUppercase(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_LETTERS:
                    if (!containsLetters(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_NUMBER:
                    if (!containsNumber(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_SPECIAL_CHARACTER:
                    if (!containsSpecialCharacters(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case CONTAINS_ALPANUMERIC:
                    if (!containsAlphanumeric(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
                case ALPHANUMERIC_ONLY:
                    if (containsSpecialCharacters(password)) {
                        return validationType.getValidationResultType();
                    }
                    break;
            }
        }

        // passed all checks
        return ValidationResultType.VALID;
    }

    private boolean invalidLength(String password) {
        return password.length() < mMinLength || password.length() > mMaxLength;
    }

    private boolean containsLowercase(String password) {
        return password.matches(".*[a-z].*");
    }

    private boolean containsUppercase(String password) {
        return password.matches(".*[A-Z].*");
    }

    private boolean containsLetters(String password) {
        return password.matches(".*[a-zA-Z].*");
    }

    private boolean containsNumber(String password) {
        return password.matches(".*[0-9].*");
    }

    private boolean containsAlphanumeric(String password) {
        return password.matches(".*\\w+.*");
    }

    private boolean containsSpecialCharacters(String password) {
        return password.matches(".*\\W+.*");
    }

    public enum ValidationType {
        LENGTH(ValidationResultType.INVALID_LENGTH),
        CONTAINS_LOWERCASE(ValidationResultType.MISSING_LOWERCASE),
        CONTAINS_UPPERCASE(ValidationResultType.MISSING_UPPERCASE),
        CONTAINS_LETTERS(ValidationResultType.MISSING_LETTERS),
        CONTAINS_NUMBER(ValidationResultType.MISSING_NUMBER),
        CONTAINS_SPECIAL_CHARACTER(ValidationResultType.MISSING_SPECIAL_CHARACTER),
        CONTAINS_ALPANUMERIC(ValidationResultType.MISSING_ALPHANUMERIC),
        ALPHANUMERIC_ONLY(ValidationResultType.INVALID_CHARACTERS);

        private ValidationResultType mValidationResultType;

        private ValidationType(ValidationResultType validationResultType) {
            mValidationResultType = validationResultType;
        }

        public ValidationResultType getValidationResultType() {
            return mValidationResultType;
        }
    }

    // TODO add failure reasons to enum
    public enum ValidationResultType {
        VALID,
        INVALID_LENGTH,
        MISSING_LOWERCASE,
        MISSING_UPPERCASE,
        MISSING_LETTERS,
        MISSING_NUMBER,
        INVALID_CHARACTERS,
        MISSING_ALPHANUMERIC,
        MISSING_SPECIAL_CHARACTER,
    }
}
