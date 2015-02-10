/*
 * Copyright 2013 Artur Mkrtchyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iban4j;

import static org.iban4j.BicFormatException.BicFormatViolation.*;

/**
 * Bic Utility Class
 */
public class BicUtil {

    private static final int BIC8_LENGTH = 8;
    private static final int BIC11_LENGTH = 11;

    private static final int BANK_CODE_INDEX = 0;
    private static final int BANK_CODE_LENGTH = 4;
    private static final int COUNTRY_CODE_INDEX = BANK_CODE_INDEX + BANK_CODE_LENGTH;
    private static final int COUNTRY_CODE_LENGTH = 2;
    private static final int LOCATION_CODE_INDEX = COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH;
    private static final int LOCATION_CODE_LENGTH = 2;
    private static final int BRANCH_CODE_INDEX = LOCATION_CODE_INDEX + LOCATION_CODE_LENGTH;
    private static final int BRANCH_CODE_LENGTH = 3;

    /**
     * Validates bic.
     *
     * @param bic to be validated.
     * @throws BicFormatException if bic is invalid.
     *         UnsupportedCountryException if bic's country is not supported.
     */
    public static void validate(final String bic) throws BicFormatException,
            UnsupportedCountryException {
        try {
            validateEmpty(bic);
            validateLength(bic);
            validateCase(bic);
            validateBankCode(bic);
            validateCountryCode(bic);
            validateLocationCode(bic);

            if(hasBranchCode(bic)) {
                validateBranchCode(bic);
            }
        } catch (UnsupportedCountryException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new BicFormatException(UNKNOWN, e.getMessage());
        }
    }

    private static void validateEmpty(final String bic) {
        if(bic == null) {
            throw new BicFormatException(BIC_NOT_NULL,
                    "Null no puede ser un Bic válido.");
        }

        if(bic.length() == 0) {
            throw new BicFormatException(BIC_NOT_EMPTY,
                    "Un string vacío no puede ser un Bic válido.");
        }
    }

    private static void validateLength(final String bic) {
        if(bic.length() != BIC8_LENGTH && bic.length() != BIC11_LENGTH) {
            throw new BicFormatException(BIC_LENGTH_8_OR_11,
                    String.format("La longitud del Bic debe de ser %d o %d",
                            BIC8_LENGTH, BIC11_LENGTH));
        }
    }

    private static void validateCase(final String bic) {
        if(!bic.equals(bic.toUpperCase())) {
            throw new BicFormatException(BIC_ONLY_UPPER_CASE_LETTERS,
                    "El Bic debe contener sólo letras mayúsculas.");
        }
    }

    private static void validateBankCode(final String bic) {
        String bankCode = getBankCode(bic);
        for(final char ch : bankCode.toCharArray()) {
            if(!Character.isLetter(ch)) {
                throw new BicFormatException(BANK_CODE_ONLY_LETTERS, ch,
                        "El código del banco debe contener solo letras.");
            }
        }
    }

    private static void validateCountryCode(final String bic) {
        final String countryCode = getCountryCode(bic);
        if(countryCode.trim().length() < COUNTRY_CODE_LENGTH ||
                !countryCode.equals(countryCode.toUpperCase()) ||
                !Character.isLetter(countryCode.charAt(0)) ||
                !Character.isLetter(countryCode.charAt(1))) {
            throw new BicFormatException(COUNTRY_CODE_ONLY_UPPER_CASE_LETTERS,
                    countryCode,
                    "Código de país Bic debe contener letras mayúsculas");
        }

        if(CountryCode.getByCode(countryCode) == null) {
            throw new UnsupportedCountryException(countryCode,
                    "El código de país no está soportado.");
        }
    }

    private static void validateLocationCode(final String bic) {
        final String locationCode = getLocationCode(bic);
        for(char ch : locationCode.toCharArray()) {
            if(!Character.isLetterOrDigit(ch)) {
                throw new BicFormatException(LOCATION_CODE_ONLY_LETTERS_OR_DIGITS,
                        ch, "El código de localización solo debe contener letras o dígitos.");
            }
        }
    }

    private static void validateBranchCode(final String bic) {
        final String branchCode = getBranchCode(bic);
        for(final char ch : branchCode.toCharArray()) {
            if(!Character.isLetterOrDigit(ch)) {
                throw new BicFormatException(BRANCH_CODE_ONLY_LETTERS_OR_DIGITS,
                        ch, "Código de la sucursal debe contener sólo letras o dígitos.");
            }
        }
    }

    static String getBankCode(final String bic) {
        return bic.substring(BANK_CODE_INDEX, BANK_CODE_INDEX + BANK_CODE_LENGTH);
    }

    static String getCountryCode(final String bic) {
        return bic.substring(COUNTRY_CODE_INDEX, COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH);
    }

    static String getLocationCode(final String bic) {
        return bic.substring(LOCATION_CODE_INDEX, LOCATION_CODE_INDEX + LOCATION_CODE_LENGTH);
    }

    static String getBranchCode(final String bic) {
        return bic.substring(BRANCH_CODE_INDEX, BRANCH_CODE_INDEX + BRANCH_CODE_LENGTH);
    }

    static boolean hasBranchCode(final String bic) {
        return bic.length() == BIC11_LENGTH;
    }
}
