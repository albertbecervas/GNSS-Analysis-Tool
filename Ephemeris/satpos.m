function [satp,satv]    =   satpos(t,eph,str)
%SATPOS   Calculation of X,Y,Z coordinates and velocity at time t
%         for given ephemeris eph

% Based on Kai Borre 04-09-96
%Copyright (c) by Kai Borre
%$Revision: 1.0 $  $Date: 1997/09/26  $

if strcmp(str,'GPS')
    GM  =   3.986008e14;             % earth's universal gravitational
    % GM = 3.986008e14;
else
    GM  =   3.986004418e14;
end
%        
% parameter m^3/s^2
Omegae_dot = 7.2921151467e-5; % earth rotation rate, rad/s
%
if( size(eph,2) < 31 ) % if GPS/GAL
    %  Units are either seconds, meters, or radians
    %  Assigning the local variables to eph
    M0      =   eph(3);
    roota   =   eph(4);
    deltan  =   eph(5);
    ecc     =   eph(6);
    omega   =   eph(7);
    cuc     =   eph(8);
    cus     =   eph(9);
    crc     =  eph(10);
    crs     =  eph(11);
    i0      =  eph(12);
    idot    =  eph(13);
    cic     =  eph(14);
    cis     =  eph(15);
    Omega0  =  eph(16);
    Omegadot=  eph(17);
    toe     =  eph(18);

    % Procedure for coordinate calculation
    A = roota*roota;
    tk = check_t(t-toe);
    n0 = sqrt(GM/A^3);
    n = n0+deltan;
    M = M0+n*tk;
    M = rem(M+2*pi,2*pi);
    E = M;
    for i = 1:10
       E_old = E;
       E = M+ecc*sin(E);
       dE = rem(E-E_old,2*pi);
       if abs(dE) < 1.e-12
          break;
       end
    end
    E = rem(E+2*pi,2*pi);
    v = atan2(sqrt(1-ecc^2)*sin(E), cos(E)-ecc);
    phi = v+omega;
    phi = rem(phi,2*pi);
    u = phi              + cuc*cos(2*phi)+cus*sin(2*phi);
    r = A*(1-ecc*cos(E)) + crc*cos(2*phi)+crs*sin(2*phi);
    i = i0+idot*tk       + cic*cos(2*phi)+cis*sin(2*phi);
    Omega = Omega0+(Omegadot-Omegae_dot)*tk-Omegae_dot*toe;
    Omega = rem(Omega+2*pi,2*pi);
    x1 = cos(u)*r;
    y1 = sin(u)*r;
    satp(1,1) = x1*cos(Omega)-y1*cos(i)*sin(Omega);
    satp(2,1) = x1*sin(Omega)+y1*cos(i)*cos(Omega);
    satp(3,1) = y1*sin(i);

    %- Compute satellite velocity
    Ek          =   E;
    fk          =   v;
    phik        =   phi;
    uk          =   u;
    x1k         =   x1;
    y1k         =   y1;
    ik          =   i;
    xk          =   satp(1);
    yk          =   satp(2);
    %
    Omegak      =   Omega;
    Ek_dot      =   n/(1-ecc*cos(Ek));
    fk_dot      =   sin(Ek)*Ek_dot*(1+ecc*cos(fk)) / ((1-cos(Ek)*ecc)*sin(fk));
    phik_dot    =   fk_dot;
    uk_dot      =   phik_dot + 2*(cus*cos(2*phik)-cuc*sin(2*phik))*phik_dot;
    rk_dot      =   A*ecc*sin(Ek)*Ek_dot + 2*(crs*cos(2*phik)-crc*sin(2*phik))*phik_dot;
    ik_dot      =   idot + 2*(cis*cos(2*phik)-cic*sin(2*phik))*phik_dot;
    Omegak_dot  =   Omegadot - Omegae_dot;
    x1k_dot     =   rk_dot*cos(uk) - y1k*uk_dot;
    y1k_dot     =   rk_dot*sin(uk) + x1k*uk_dot;
    xk_dot      =   x1k_dot*cos(Omegak) - y1k_dot*cos(ik)*sin(Omegak) + y1k*sin(ik)*sin(Omegak)*ik_dot - yk*Omegak_dot;
    yk_dot      =   x1k_dot*sin(Omegak) + y1k_dot*cos(ik)*cos(Omegak) - y1k*sin(ik)*ik_dot*cos(Omegak) + xk*Omegak_dot;
    zk_dot      =   y1k_dot*sin(ik) + y1k*cos(ik)*ik_dot;

    satv        =   [xk_dot; yk_dot; zk_dot];
else % GLO satellite coordinates computation
    A           =   6378136;
    GM          =   398600.44178e9;
    J2          =   1082625.7e-9;
    omegae_dot  =   7.292115e-5;
    time_eph = Eph(32); %ephemeris reference time

    X   = Eph(5);  %satellite X coordinate at ephemeris reference time
    Y   = Eph(6);  %satellite Y coordinate at ephemeris reference time
    Z   = Eph(7);  %satellite Z coordinate at ephemeris reference time

    Xv  = Eph(8);  %satellite velocity along X at ephemeris reference time
    Yv  = Eph(9);  %satellite velocity along Y at ephemeris reference time
    Zv  = Eph(10); %satellite velocity along Z at ephemeris reference time

    Xa  = Eph(11); %acceleration due to lunar-solar gravitational perturbation along X at ephemeris reference time
    Ya  = Eph(12); %acceleration due to lunar-solar gravitational perturbation along Y at ephemeris reference time
    Za  = Eph(13); %acceleration due to lunar-solar gravitational perturbation along Z at ephemeris reference time
    %NOTE:  Xa,Ya,Za are considered constant within the integration interval (i.e. toe ?}15 minutes)
    
    %integration step
    int_step = 60; %[s]
    
    %time from the ephemeris reference epoch
    tk = check_t(t - time_eph);
    
    %number of iterations on "full" steps
    n = floor(abs(tk/int_step));

    %array containing integration steps (same sign as tk)
    ii = ones(n,1)*int_step*(tk/abs(tk));
    
    %check residual iteration step (i.e. remaining fraction of int_step)
    int_step_res = rem(tk,int_step);

    %adjust the total number of iterations and the array of iteration steps
    if (int_step_res ~= 0)
        n = n + 1;
        ii = [ii; int_step_res];
    end
    
    %numerical integration steps (i.e. re-calculation of satellite positions from toe to tk)
    pos = [X Y Z];
    vel = [Xv Yv Zv];
    acc = [Xa Ya Za];

    for s = 1 : n

        %Runge-Kutta numerical integration algorithm
        %
        %step 1
        pos1 = pos;
        vel1 = vel;
        [pos1_dot, vel1_dot] = satellite_motion_diff_eq(pos1, vel1, acc, A, GM, J2, omegae_dot);
        %
        %step 2
        pos2 = pos + pos1_dot*ii(s)/2;
        vel2 = vel + vel1_dot*ii(s)/2;
        [pos2_dot, vel2_dot] = satellite_motion_diff_eq(pos2, vel2, acc, A, GM, J2, omegae_dot);
        %
        %step 3
        pos3 = pos + pos2_dot*ii(s)/2;
        vel3 = vel + vel2_dot*ii(s)/2;
        [pos3_dot, vel3_dot] = satellite_motion_diff_eq(pos3, vel3, acc, A, GM, J2, omegae_dot);
        %
        %step 4
        pos4 = pos + pos3_dot*ii(s);
        vel4 = vel + vel3_dot*ii(s);
        [pos4_dot, vel4_dot] = satellite_motion_diff_eq(pos4, vel4, acc, A, GM, J2, omegae_dot);
        %
        %final position and velocity
        pos = pos + (pos1_dot + 2*pos2_dot + 2*pos3_dot + pos4_dot)*ii(s)/6;
        vel = vel + (vel1_dot + 2*vel2_dot + 2*vel3_dot + vel4_dot)*ii(s)/6;
    end

    %transformation from PZ-90.02 to WGS-84 (G1150)
    satp(1,1) = pos(1) - 0.36;
    satp(2,1) = pos(2) + 0.08;
    satp(3,1) = pos(3) + 0.18;
    
    %satellite velocity
    satv(1,1) = vel(1);
    satv(2,1) = vel(2);
    satv(3,1) = vel(3);        
        
end



%%%%%%%%% end satpos.m %%%%%%%%%
