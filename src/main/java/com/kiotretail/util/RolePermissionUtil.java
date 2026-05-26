package com.kiotretail.util;

/**
 * Role permission helper based on RDS screen authorization.
 */
public class RolePermissionUtil {

    public static boolean isAdmin(String roleName) {
        return equalsRole(roleName, "Admin") || equalsRole(roleName, "Quản trị hệ thống");
    }

    public static boolean isOwner(String roleName) {
        return equalsRole(roleName, "Owner");
    }

    public static boolean isStoreManager(String roleName) {
        return equalsRole(roleName, "StoreManager");
    }

    public static boolean isSalesStaff(String roleName) {
        return equalsRole(roleName, "SalesStaff");
    }

    public static boolean isWarehouseStaff(String roleName) {
        return equalsRole(roleName, "WarehouseStaff");
    }

    public static boolean canViewCategory(String roleName) {
        return isAdmin(roleName) || isOwner(roleName) || isStoreManager(roleName)
                || isSalesStaff(roleName) || isWarehouseStaff(roleName);
    }

    public static boolean canManageCategory(String roleName) {
        return isAdmin(roleName) || isOwner(roleName);
    }

    public static boolean canViewRoleManagement(String roleName) {
        return isAdmin(roleName) || isOwner(roleName);
    }

    public static boolean canAccessManagementArea(String roleName) {
        return canViewCategory(roleName) || canViewRoleManagement(roleName);
    }

    public static boolean canAccessPos(String roleName) {
        return isAdmin(roleName) || isOwner(roleName) || isStoreManager(roleName) || isSalesStaff(roleName);
    }

    private static boolean equalsRole(String actual, String expected) {
        return actual != null && actual.trim().equalsIgnoreCase(expected);
    }
}
