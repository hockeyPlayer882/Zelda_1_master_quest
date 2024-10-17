package util;

/**
 * Velocity's versioning struct.
 */
public class Version {
    /**
     * Velocity revision number.
     */
    int v;

    /**
     * Velocity major version number.
     */
    int major;

    /**
     * Velocity minor version number.
     */
    int minor;

    /**
     * Velocity patch number.
     */
    int patch;

    /**
     * Create a version representation.
     * 
     * @param major Major version number.
     * @param minor Minor version number.
     * @param patch Patch number.
     */
    public Version(int major, int minor, int patch) {
        this.v = 1;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Create a version representation.
     * 
     * @param v Application revision number.
     * @param major Major version number.
     * @param minor Minor version number.
     * @param patch Patch number.
     */
    public Version(int v, int major, int minor, int patch) {
        this.v = v;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Parse a version from a string.
     * @deprecated This function is no longer used across the code base.
     * 
     * @param all All numbers in a string.
     */
    @Deprecated(since="v0.5.2.0", forRemoval=true)
    public Version(String all) {
        if (all.length() != 7) {
            return;
        }
        this.v = Integer.parseInt(all.substring(0, 1));
        this.major = Integer.parseInt(all.substring(1, 3));
        this.minor = Integer.parseInt(all.substring(3, 5));
        this.patch = Integer.parseInt(all.substring(5, 7));
    }

    /**
     * Check if this current version is newer than the provided one.
     * 
     * @param other The other version.
     * @return If this version is newer.
     */
    public boolean isNewer(Version other) {
        if (this.v > other.v) { return true; }
        else if (this.major > other.major) { return true; }
        else if (this.minor > other.minor) { return true; }
        else if (this.patch > other.patch) { return true; }
        return false;
    }

    /**
     * Check if the other version provided is older than this current version.
     * 
     * @param other The other version.
     * @return If the version is older.
     */
    public boolean isOlder(Version other) {
        if (this.v > other.v) { return false; }
        else if (this.major > other.major) { return false; }
        else if (this.minor > other.minor) { return false; }
        else if (this.patch > other.patch) { return false; }
        return true;
    }

    /**
     * Check if the two versions are equal.
     * 
     * @param other The other version.
     * @return If the two versions are equivalent.
     */
    public boolean equals(Version other) {
        return this.v == other.v && this.major == other.major && this.minor == other.minor
            && this.patch == other.patch;
    }

    /**
     * @deprecated Remnants of an unused API.
     * @return This converted to a string.
     */
    @Deprecated(since="v0.5.2.0", forRemoval=true)
    public String toIntString() {
        String out = "" + this.v;
        out += ("" + (this.major + 100)).substring(1, 3);
        out += ("" + (this.minor + 100)).substring(1, 3);
        out += ("" + (this.patch + 100)).substring(1, 3);
        return out;
    }

    /**
     * Represent this object as an easily readable string.
     * 
     * @return This object's readable string representation.
     */
    public String toString() {
        return "v"+ v + "." + major + "." + minor + "." + patch;
    }
}