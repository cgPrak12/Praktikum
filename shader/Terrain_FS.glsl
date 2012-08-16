#version 150 core

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
in vec3 tangent;
in vec3 binormal;

in vec2 tex;

uniform sampler2D elevation;
out vec4 finalColor;

void main(void)
{	
	vec3 fragmentColor = vec3(1,1,1);
	//finalColor = vec4(binormal,1); return;
	

	if(materialV<2.5){
		fragmentColor = seaColor;
	}else{
		if(materialV<3.5){
			fragmentColor = sandColor;
		}else{
			if(materialV<4.5){
				fragmentColor = earthColor;
			}else{
				if(materialV<5.5){
					fragmentColor = grassColor;
				}else{
					if(materialV<6.5){
						fragmentColor = darkGrassColor;
					}else{
						if(materialV<7.5){
							fragmentColor = stoneColor;
						}else{
							if(materialV<8.5){
								fragmentColor = rockColor;
							}else{
								if(materialV<10.5){
									fragmentColor = snowColor;
								}else{
									if(materialV<21.5){
										fragmentColor = earthColor;
									}else{
										if(materialV<22.5){
											fragmentColor = seaColor;
										}else{
											if(materialV<23.5){
												fragmentColor = sandColor;
											}
										}
									}									
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
	//finalColor = vec4(binormal, 1.0);
}