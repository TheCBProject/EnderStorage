package codechicken.enderstorage.reference;

import codechicken.lib.util.ArrayUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class VariantReference {

    public static final String[] enderBlockNames = new String[] { "enderChest", "enderTank" };
    public static final List<String> enderBlockNamesList = Lists.newArrayList(ArrayUtils.arrayToLowercase(enderBlockNames));

}
