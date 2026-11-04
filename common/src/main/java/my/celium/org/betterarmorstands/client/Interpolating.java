package my.celium.org.betterarmorstands.client;

/** Added to every entity by a mixin: stops the client's movement interpolation toward the last server update. */
public interface Interpolating {
    void betterarmorstands$stopInterpolation();
}
