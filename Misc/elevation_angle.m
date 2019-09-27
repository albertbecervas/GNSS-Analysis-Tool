function alpha = elevation_angle(r_u, r_s)
% ELEVATION ANGLE:  Returns the elevation angle of the satellite over
%                   the horizon with respect to the user's position.
% Input:
%           r_u:    Vector defining the user's position.
%           r_s:    Vector defining the satellite's position.
% Input:
%           alpha:  Angle expressed in rad of the satellite over
%                   the horizon with respect to the user's position.

    d       =   r_u - r_s;      % Vector 'distance' between user and satellite
    
    gamma   =   acos(dot(r_u, d) / (norm(r_u) * norm(d)));  % Angle between r_u and d
    
    alpha   =   gamma - pi/2;

end