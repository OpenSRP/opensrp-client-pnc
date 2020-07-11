package org.smartregister.pnc.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public interface FilePath {

    interface FOLDER {

        String CONFIG_FOLDER_PATH = "config/";
    }

    interface FILE {

        String PNC_PROFILE_OVERVIEW = "pnc-profile-overview.yml";
        String PNC_PROFILE_VISIT = "pnc-profile-visits.yml";
    }
}