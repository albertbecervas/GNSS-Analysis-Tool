function    [PVT,DOP,GPS,GAL,NS,error]  =   PVT_recLS_multiC(acq_info,epht,PVT0)
% PVT_recLS:    Computation of the receiver position at time TOW from  
%               pseudoranges (pr) and ephemerides information (eph). 
%               Implementation using the iterative Least-Squares principle 
%               after linearization of the navigation equations. We use the
%               svn Id (svn) to get the satellite coordinates corresponding 
%               to the pseudoranges given by the input (pr).
%               
% Input:            
%               acq_info:   Struct with all the acquisition information
%                           extracted from the phone.
%               eph:        Struct with all the matrix ephemeris 
%                           information for different constellations
%
% Output:       PVT:        Nsolx1 vector with the estimated PVT solution                  
%
        
    %% General initializations
    c                   =   299792458; 
    Nit                 =   5; 
    emptysat            =   [];
%     GPS_tropoCorr       =   0;
%     GPS_ionoCorr        =   0;
%     Galileo_tropoCorr   =   0;
%     Galileo_ionoCorr    =   0;
    error.flag          =   0;
    error.text          =   '';
%     e_t                 =   0;
    %
    PVT     =   PVT0;%[acq_info.refLocation.XYZ 0];
    iono    =   acq_info.ionoProto;
    %
    flgGPS              =   acq_info.flags.constellations.GPS;
    flgGAL              =   acq_info.flags.constellations.Galileo;
    %
    % GPS init
    if flgGPS
        flgL1       =   acq_info.flags.constellations.gpsL1;
        measL1      =   acq_info.satellites.gpsSatellites.gpsL1;
        measL5      =   acq_info.satellites.gpsSatellites.gpsL5;
        flgL5       =   acq_info.flags.constellations.gpsL5;
        flg2        =   acq_info.flags.corrections.f2corr;
        TOW         =   acq_info.tow;
        %
        [GPS,~]  =   initParam(flgL1,flgL5,measL1,measL5,epht,flg2,TOW,'GPS');
        % Check if eph is further used (if not replace by ~, if so control
        % when GPS_eph or GAL_eph)
    else
        GPS.pr          =   [];
        GPS.svn         =   [];
        GPS.cn0         =   [];
        GPS.ionoCorr    =   [];
        GPS.tcorr       =   [];
        GPS.X           =   [];
        GPS.nsat        =   0;
    end
    % GAL init
    if flgGAL
        flgL1       =   acq_info.flags.constellations.galE1;
        flgL5       =   acq_info.flags.constellations.galE5a;
        %
        measL1      =   acq_info.satellites.galSatellites.galE1;
        measL5      =   acq_info.satellites.galSatellites.galE5a;
        %
        flg2        =   acq_info.flags.corrections.f2corr;
        TOW         =   acq_info.tow;
        %
        [GAL,~]  =   initParam(flgL1,flgL5,measL1,measL5,epht,flg2,TOW,'GAL');
        % Check if eph is further used (if not replace by ~, if so control
        % when GPS_eph or GAL_eph)
        
    else
        GAL.pr          =   [];
        GAL.svn         =   [];
        GAL.cn0         =   [];
        GAL.ionoCorr    =   [];
        GAL.tcorr       =   [];
        GAL.X           =   [];
        GAL.nsat        =   0;
    end
    % Multi-Const init    
    if( flgGAL && flgGPS )
        flgMC           =   1;
        N1              =   GPS.nsat;
        N2              =   GAL.nsat;
    else
        flgMC           =   0;
    end
    pr                  =   [GPS.pr;GAL.pr];
    svn                 =   [GPS.svn;GAL.svn];
    cn0                 =   [GPS.cn0;GAL.cn0];
    ionoCorr            =   [GPS.ionoCorr;GAL.ionoCorr];
    tcorr               =   [GPS.tcorr;GAL.tcorr];
    X                   =   [GPS.X GAL.X];
    Nsat                =   length(svn);
    %
    %% LS loop
    if( Nsat >= 3 + flgGPS + flgGAL )
        for iter = 1:Nit
            [pd,A,tropoCorr,ionoCorr]   =   getMeasandState(acq_info,X,PVT,iono,ionoCorr,tcorr,pr);
            if flgMC
                if( iter == Nit )
                    GPS.tropoCorr           =   tropoCorr(1:N1);
                    GAL.tropoCorr           =   tropoCorr(N1+1:end);
                    %
                end
                % Check this MC matrix
                A       =   [A ones(Nsat,1) [zeros(N1,1);ones(N2,1)]];                
            else
                if( iter == Nit )
                    if flgGPS
                        GPS.tropoCorr           =   tropoCorr;
                    else
                        GAL.tropoCorr           =   tropoCorr;
                    end
                    %
                end
                %- Add last column
                A       =   [A ones(Nsat,1)];
            end
            % Delete rows of 0s (Esto para que es???)
            if iter == 1
                for i=1:size(A, 1)
                    if A(i,:) == 0
                        emptysat = [emptysat i];
                    end
                end
                emptysat = emptysat(length(emptysat):-1:1);
                for i=1:length(emptysat)
                   A(emptysat(i),:) = [];
                   pd(emptysat(i))   = [];
                end
            end
            emptysat         = []; 
            %
            for ii = 1:length(pd)
                [~, eL(ii), ~]      =   topocent(PVT(1:3),X(:,ii));    % Satellite azimuth and elevation
            end
            %- Apply Mask -%
            if  acq_info.flags.algorithm.mask.flag
            %             type        =   acq_info.flags.algorithm.mask.type;
            % De momento s?lo aplico por elevaci?n
            %
                maskVal     =   acq_info.flags.algorithm.mask.value;
                idx         =   eL > maskVal;
                A       =   A(idx,:);
                pd       =   pd(idx);
                cn0     =   cn0(idx);
                eL      =   eL(idx);            
            end  
            %
            
            %- Get rid off outliers -%
            tmp     =   pd<1e5;
            A       =   A(tmp,:);
            pd       =   pd(tmp);
            cn0     =   cn0(tmp);
            eL      =   eL(tmp); 
            %
        
            %- Weighting matrix
            if acq_info.flags.algorithm.WLS
                W   =   compute_Wmatrix(eL,cn0,1);
            else
                W   =   eye(length(A));
            end
            %- LS
            H               =   inv(A'*W*A);
            d               =   H*A'*W*pd;            % PVT update            
            %- Integrity Check (RAIM) -%
            if( length(pd) > 4 ) % Fault Exclusion
                resi            =   pd-A*d;
                SSE             =   sqrt((resi'*resi)/length(pd)-4);   % Sum of Square Errors (SSE) for RAIM
                while( SSE > 1e3 && length(pd) > 4)
                    [~,idx]     =   max(resi);
                    idt         =   ~(1:length(pd) == idx);
                    pd          =   pd(idt);
                    A           =   A(idt,:);
                    W           =   W(idt,idt);
                    H           =   inv(A'*W*A);
                    d           =   H*A'*W*pd;
                    %
                    resi        =   pd - A*d;
                    SSE         =   sqrt((resi'*resi)/(length(pd)-4));
                end
            else        % Fault detection --> PVT exclusion (i.e. nan)
    %                     resi            =   p-G*d;
    %                     SSE             =   sqrt((resi'*resi));   % Sum of Square Errors (SSE) for RAIM
    %                     if( SSE > 1e3 )
    %                         d           =   nan(4,1); % Ojo luego al promediar, al promediar trata de utilizar nanmean()
    %                     end
            end
            %- PVT update
            PVT(1:3)    =   PVT(1:3) + d(1:3);  % Update the PVT coords.
            PVT(4)      =   d(4);     % Receiver clock offset
        end
        %
        GDOP            =   sqrt(H(1,1) + H(2,2) + H(3,3) + H(4,4));
        PDOP            =   sqrt(H(1,1) + H(2,2) + H(3,3));
        TDOP            =   sqrt(H(4,4));
        DOP             =   [GDOP, PDOP, TDOP];           
        NS              =   size(A, 1);    
        %
    else % Not enough satelites
        error.flag      =   1;
        error.text      =   'Not enough satellites';
        PVT             =   PVT0;%[0 0 0 0];
        DOP             =   [];
        Corr            =   [];
        NS              =   0;
    end
    %
    
        %
end
     
