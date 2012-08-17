#version 150 core
uniform sampler2D color;
// Colors
const vec3 seaColor = vec3(0.0, 0.0, 1.0);	
const vec3 sandColor = vec3(0.9, 0.9, 0.75);	
const vec3 earthColor = vec3(0.45, 0.275, 0.078); 
const vec3 grassColor = vec3(0.0, 0.5, 0.0); 
const vec3 darkGrassColor = vec3(0.0, 0.2, 0.0);
const vec3 stoneColor = vec3(0.5, 0.5, 0.5); 
const vec3 rockColor = vec3(0.35, 0.35, 0.35); 
const vec3 snowColor = vec3(0.9, 0.9, 1.0);

const vec3 inverseLightDir = vec3(1.0, 1.0, 0.0);
const float diff = 0.4;

in float height;
in vec3 normalWC;
in float materialV;
in vec2 tex;

out vec4 finalColor;



void main(void)
{	
	vec3 fragmentColor = vec3(1,1,1);

	if(texture(color, tex).r<2.5){
		fragmentColor = seaColor;
	}else{
		if(texture(color, tex).r<3.5){
			fragmentColor = sandColor;
		}else{
			if(texture(color, tex).r<4.5){
				fragmentColor = earthColor;
			}else{
				if(texture(color, tex).r<5.5){
					fragmentColor = grassColor;
				}else{
					if(texture(color, tex).r<6.5){
						fragmentColor = darkGrassColor;
					}else{
						if(texture(color, tex).r<7.5){
							fragmentColor = stoneColor;
						}else{
							if(texture(color, tex).r<8.5){
								fragmentColor = rockColor;
							}else{
								if(texture(color, tex).r<9.5){
									fragmentColor = snowColor;
								}
							}
						}
					}
				}
			}
		}
	}

	fragmentColor *= max(0, dot(normalize(inverseLightDir), normalize(normalWC)));
	finalColor = vec4(fragmentColor, 1.0);
}