#version 330 core

out vec4 FragColor;

in VertAttrs {
    vec2 TexCoords;
} v_VertAttrs;

uniform vec3 u_Color;

void main() {
    vec4 color = vec4(u_Color, 1.0f);
    FragColor = color;
}