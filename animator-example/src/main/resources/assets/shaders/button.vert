#version 330 core

layout (location = 0) in vec2 a_Pos;
layout (location = 1) in vec2 a_TexCoords;

uniform mat4 u_ModelMat;

out VertAttrs {
    vec2 TexCoords;
} v_VertAttrs;

void main() {
    gl_Position = u_ModelMat * vec4(a_Pos, 1.0f, 1.0f);
    v_VertAttrs.TexCoords = a_TexCoords;
}