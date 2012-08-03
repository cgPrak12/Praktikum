#version 150 core
uniform sampler2D colorTex; //textur laden

in vec2 fragmentTexCoords;

out vec4 fragmentColor;

const vec4 color = vec4(0.0, 1.0, 0.0, 1.0); // blue

void main(void) {
//    fragColor = texture(colorTex, fragmentTexCoords);
    fragmentColor = color;
}