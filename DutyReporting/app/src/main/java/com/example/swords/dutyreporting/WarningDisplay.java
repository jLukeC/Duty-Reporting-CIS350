package com.example.swords.dutyreporting;

import java.util.Set;

/**
 * Created by aolesky on 4/30/15.
 */

/**
 * Interface used to display warnings of a user
 */
public interface WarningDisplay {
    public void addWarnings(Set<String> warnings, String resident);
}
