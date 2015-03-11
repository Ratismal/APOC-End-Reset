package org.apocgaming.endresetold;

public class APOCChunk {
	int x, z;
	
	public APOCChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APOCChunk other = (APOCChunk) obj;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
	
	
	
}
