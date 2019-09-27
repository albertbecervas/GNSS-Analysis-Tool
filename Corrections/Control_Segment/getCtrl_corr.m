function    [X, tcorr,tgd]   =   getCtrl_corr(eph, svn, TOW, pr,str)
% getSatPos_tcorr:  Get satellite coordinates and clock correction.  
%
% Inputs:
%           - eph:  Ephemeris data obtained from the Navigation RINEX file
%                   cointaining the data needed for the computation of the
%                   satellite coordinates and clock corrections.
%           - svn:  Integer with the sat. ID of the satellite to be analyzed
%           - TOW:  TOW of the epoch to analyze.
%           - pr:   Pseudorange measure at given TOW
% Outputs:
%           - X:        3x1 matrix with the corrected cartesian coordinates 
%                       of the satellite with Id given by svn at the given 
%                       TOW.
%           - tcorr:    Clock correction in [sec.] for the satellite with  
%                       Id given by svn at the given TOW.
%

    %
    c           =   299792458;          %   Speed of light (m/s)
    %-  Get satellite coordinates and clock corrections
    tx_RAW      =   TOW - pr/c;         %   1st guess of Tx time
    %       
    %--     Identify ephemerides columns in eph
    col         =   find_eph(eph, svn, tx_RAW);
    %
    %--     Get clock corrections
    tcorr       =   sat_clock_error_correction(tx_RAW, eph(:, col));
    tgd         =   eph(22, col);
%     tcorr       =   tcorr - tgd;        %   Correct the total Group Delay (TGD)
    tx_GPS      =   tx_RAW-tcorr;       %   Correct the Tx time
    %--     Compute again the clock bias
    tcorr       =   sat_clock_error_correction(tx_GPS, eph(:, col));
%     tcorr       =   tcorr - tgd;    
    %
    %-  Get satellite coordinates (corrected) and velocity
    [X, vel]     =   satpos(tx_GPS, eph(:, col),str);    
    %
    %--     Get the satellite relativistic clock correction
    trel        =   -2 * ( dot(X, vel) / (c^2) ); % IS-GPS-200E, p. 86

    %
    %--     Account for the relativistic effect on the satellite clock bias
    %       and the time of transmission
    tcorr       =   tcorr + trel;   
    tx_GPS      =   tx_RAW - tcorr;  % Corrected GPS time
    %
    %--     Recompute the satellite coordinates with the corrected time and
    %       some additional correction (i.e. Sagnac effect / rotation
    %       correction
    traveltime  =   TOW - tx_GPS;
    X           =   e_r_corr(traveltime, X);

end