#version 150 core

const vec3 upColor = vec3(0.9, 0.9, 1.0);	// white
const vec3 midColor = vec3(0.0, 0.5, 0.0);	// green
const vec3 lowColor = vec3(0.9, 0.9, 0.75); // beige
const vec3 downColor = vec3(0.0, 0.0, 1.0); // blue
const vec3 inverseLightDir = vec3(1.0, 1.0, 0.0);
const float diff = 0.4;

in float height;
in vec3 normalWC;

out vec4 finalColor;

void main(void)
{	
	vec3 fragmentColor = vec3(1,1,1);
	
	
	
	if(height<0-diff){
	fragmentColor = downColor;
		}else{
			if(height<1-diff){
			fragmentColor = mix(downColor,lowColor,(height+diff)/2);
			}else{
				if(height<1.5-diff){
				fragmentColor = lowColor;
				}else{
					if(height<2-diff){
					fragmentColor = mix(lowColor, midColor, ((height+diff)-1.5)*2);
					}else{
						if(height<2.5-diff){
						fragmentColor = midColor;
						}else{
							if(height<3-diff){
							fragmentColor = mix(midColor, upColor, ((height+diff)-2.5)*2);
							}else{
								fragmentColor = upColor;
								}
							}
						}
					}
				}
			}
	fragmentColor *= max(0, dot(normalize(inverseLightDir), normalize(normalWC)));
	finalColor = vec4(fragmentColor, 1.0);
	finalColor = vec4(normalWC, 1.0);
}