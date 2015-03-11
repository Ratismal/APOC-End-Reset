package org.apocgaming.endreset.world;

import org.bukkit.Chunk;

import java.util.Objects;

/**
 * Created by thomas15v on 11/03/15.
 */
public class GameChunk {

    private int X;
    private int Z;

    public GameChunk(int X, int Z){
        this.X = X;
        this.Z = Z;
    }

    public GameChunk(Chunk chunk){
        this(chunk.getX(), chunk.getZ());
    }

    public int getX() {
        return X;
    }

    public int getZ() {
        return Z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X,Z);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("GameChunk(X:").append(X).append(",Y:").append(Z).append(")").toString();
    }
}
