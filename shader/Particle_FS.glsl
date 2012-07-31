#version 150 core

in float lifetime;
out vec4 finalColor;

void main(void)
{
    finalColor = vec4(0,0,1,1);
	if( lifetime <= 0.0f) 
	{
		discard;
    }
	
}