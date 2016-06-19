#version 130

uniform mat4 m_projection;
uniform mat4 m_modelview;

in vec3 v_position;
in vec2 v_uv;

out vec2 f_uv;

void
main ()
{
  gl_Position = m_projection * m_modelview * vec4 (v_position, 1.0);
  f_uv        = v_uv;
}