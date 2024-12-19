#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;      // The original texture (background)
uniform sampler2D u_newTexture;   // The new texture (explosion or any texture)
uniform vec2 u_resolution;        // Screen resolution (width, height)
uniform float u_radius;           // Radius around the touch position
uniform vec2 u_touchPos;          // Normalized touch position (x, y)

varying vec2 v_texCoord;          // Texture coordinate from vertex shader

// Function to calculate a circular mask
float circle(in vec2 _st, in float _radius, in vec2 _center) {
    vec2 st = _st - _center;
    st.x *= u_resolution.x / u_resolution.y;  // Correct for aspect ratio
    return 1.0 - smoothstep(_radius - (_radius * 0.01), _radius + (_radius * 0.01), dot(st, st) * 4.0);
}

void main() {
    vec2 st = gl_FragCoord.xy / u_resolution.xy;
    vec4 texColor = texture2D(u_texture, v_texCoord);
    vec4 newTexColor = texture2D(u_newTexture, v_texCoord);
    float mask = circle(st, u_radius, u_touchPos);
    vec4 finalColor = mix(texColor, newTexColor, mask);
    gl_FragColor = finalColor;
}
