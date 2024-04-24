package utc.hiep.pacmanjavafx.model.world;

import utc.hiep.pacmanjavafx.lib.iVector2D;

import java.util.Objects;

public class Portal {
    private final iVector2D leftTunnelEnd;
    private final iVector2D rightTunnelEnd;
    private final int depth;

    public Portal(iVector2D leftTunnelEnd, iVector2D rightTunnelEnd, int depth) {
        this.leftTunnelEnd = leftTunnelEnd;
        this.rightTunnelEnd = rightTunnelEnd;
        this.depth = depth;
    }

    public iVector2D leftTunnelEnd() {
        return leftTunnelEnd;
    }

    public iVector2D rightTunnelEnd() {
        return rightTunnelEnd;
    }


    /**
     * @param tile tile
     * @return return true if the given tile is part of this portal, false otherwise.
     */
    public boolean contains(iVector2D tile) {
        for(int i = 1; i <= depth; i++) {
            if(tile.equals(leftTunnelEnd.minus(i, 0))) {
                return true;
            }
            if(tile.equals(rightTunnelEnd.plus(i, 0))) {
                return true;
            }
        }
        return false;
    }

    public iVector2D otherTunnel(iVector2D tunnel) {
        return tunnel.equals(leftTunnelEnd.minus(depth, 0)) ? rightTunnelEnd : leftTunnelEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(depth, leftTunnelEnd, rightTunnelEnd);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Portal other = (Portal) obj;
        return depth == other.depth && Objects.equals(leftTunnelEnd, other.leftTunnelEnd)
                && Objects.equals(rightTunnelEnd, other.rightTunnelEnd);
    }
}
