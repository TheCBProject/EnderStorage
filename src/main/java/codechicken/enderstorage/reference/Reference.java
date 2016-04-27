package codechicken.enderstorage.reference;

import codechicken.core.launch.CodeChickenCorePlugin;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class Reference {

    public static final String MOD_ID = "EnderStorage";
    public static final String MOD_NAME = "EnderStorage";
    public static final String MOD_DESCRIPTION = "Stores your stuff in the END!";

    public static final String MOD_PREFIX = MOD_ID.toLowerCase() + ":";

    public static final String VERSION = "${VERSION}";
    public static final String DEPENDENCIES = "required-after:CodeChickenCore@[" + CodeChickenCorePlugin.version + ",)";

    public static final String COMMON_PROXY = "codechicken.enderstorage.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "codechicken.enderstorage.proxy.ClientProxy";


}
