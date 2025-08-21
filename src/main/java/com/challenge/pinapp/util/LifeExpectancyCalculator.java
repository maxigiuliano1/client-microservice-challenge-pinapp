package com.challenge.pinapp.util;

import java.time.LocalDate;

public class LifeExpectancyCalculator {
    private static final int LIFE_EXPECTANCY_YEARS = 80;

    public static LocalDate calculateEstimatedDeathDate(LocalDate birthDate) {
        return birthDate.plusYears(LIFE_EXPECTANCY_YEARS);
    }
}
