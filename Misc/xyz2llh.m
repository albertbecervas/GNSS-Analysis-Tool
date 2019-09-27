function llh = xyz2llh(xyz)
% Convert from ECEF cartesian coordinates to latitude, longitude and height.
% Based on the WGS-84 reference frame
%
%   llh is a (1x3) array
%	llh(1) = latitude in radians, ranging from [-pi/2,pi/2] positive N
%	llh(2) = longitude in radians, ranging from [-pi,pi] positive E
%	llh(3) = height above ellipsoid in meters
%
%   xyz is a (1x3) array
%	xyz(1) = ECEF x-coordinate in meters
%	xyz(2) = ECEF y-coordinate in meters
%	xyz(3) = ECEF z-coordinate in meters

%	Reference: Understanding GPS: Principles and Applications,
%	           Elliott D. Kaplan, Editor, Artech House Publishers,
%	           Boston, 1996.


x = xyz(1);
y = xyz(2);
z = xyz(3);
x2 = x^2;
y2 = y^2;
z2 = z^2;

a = 6378137.0000;	% earth radius in meters
b = 6356752.3142;	% earth semiminor in meters	
e = sqrt (1-(b/a).^2);
b2 = b*b;
e2 = e*e;
ep = e*(a/b);
r = sqrt(x2+y2);
r2 = r*r;
E2 = a^2 - b^2;
F = 54*b2*z2;
G = r2 + (1-e2)*z2 - e2*E2;
c = (e2*e2*F*r2)/(G*G*G);
s = ( 1 + c + sqrt(c*c + 2*c) )^(1/3);
P = F / (3 * (s+1/s+1)^2 * G*G);
Q = sqrt(1+2*e2*e2*P);
ro = -(P*e2*r)/(1+Q) + sqrt((a*a/2)*(1+1/Q) ...
    - (P*(1-e2)*z2)/(Q*(1+Q)) - P*r2/2);
tmp = (r - e2*ro)^2;
U = sqrt( tmp + z2 );
V = sqrt( tmp + (1-e2)*z2 );
zo = (b2*z)/(a*V);

llh(3) = U*( 1 - b2/(a*V) );

llh(1) = atan( (z + ep*ep*zo)/r );

temp = atan(y/x);
if x >=0,
    llh(2) = temp;
elseif (x < 0) & (y >= 0),
    llh(2) = pi + temp;
else,
    llh(2) = temp - pi;
end
