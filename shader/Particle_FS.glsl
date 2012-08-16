#version 150 core

in float lifetime;
out vec4 finalColor;

void main(void)
{
	if (lifetime <= 0) {
		discard;
	} else {
    	finalColor = vec4(0,0,1,1);
	}

	
}