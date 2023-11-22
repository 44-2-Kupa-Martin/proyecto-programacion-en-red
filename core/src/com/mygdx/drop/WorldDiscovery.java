package com.mygdx.drop;

import java.io.Serializable;
import java.net.InetAddress;

public class WorldDiscovery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8629475529786322618L;
	public final String worldName;
    public final InetAddress IP;

    public WorldDiscovery(String worldName, InetAddress IP) {
        this.worldName = worldName;
        this.IP = IP;
    }

}
