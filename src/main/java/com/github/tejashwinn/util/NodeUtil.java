package com.github.tejashwinn.util;

import java.util.Arrays;
import java.util.UUID;

public class NodeUtil {
    public static final String NODE_ID = Arrays.toString(UuidUtil.uuidToBytes(UUID.randomUUID()));
}
