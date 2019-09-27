function    [tropoCorr, ionoCorr,eL]     =   getProp_corr(X,pos,iono,TOW)
% getProp_tcorr:  Get propagation effects correction.  
%
% Inputs:
%           - X:        Matrix with the cartesian coordinates of the given
%                       satellite
%           - pos:      Position of the user receiver
%           - iono:     Ionosphere parameters extracted from the navigation
%                       message
%           - TOW:      TOW of the epoch to analyze
%
% Outputs:
%           - tropoCorr:    Tropospheric effects correction [m] for the 
%                           satellite with position given by X at time TOW
%           - ionoCorr:     Ionospheric effects correction [m] for the 
%                           satellite with position given by X at time TOW
%           - eL:           Satellite elevations (tropospheric) for the 
%                           satellite with position given by X at time TOW
%

%
    PVT                 =   pos(1:3); 
    %--     Get corrections for propagation effects
    %
    %--     Coordinates transformation
    [aZ, eL, ~]         =   topocent(PVT,X);    % Satellite azimuth and elevation
    llh                 =   xyz2llh(PVT);       % Latitude-Longitude-Height
    %--     Get tropospheric correction (Saastamoinen model)
    tropoCorr           =   tropo_error_correction(eL,llh(3)); 
    %
    %--     Get ionospheric correction
    if( ~isempty(iono) )
        ionoCorr        =   iono_error_correction(llh(1)*180/pi,...
                            llh(2)*180/pi,aZ,eL,TOW,iono,[]);
    else
        ionoCorr        =   0.00001;
    end


end