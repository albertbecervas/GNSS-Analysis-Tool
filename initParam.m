function    [GPS,eph]  =   initParam(flgL1,flgL5,measL1,measL5,epht,flg2,TOW,const)
    
    fact                    =   1;
    if flgL1
        [prt,svnt,CN0t]     =   getMeas(measL1);
        GPS.L1.pr           =   prt;
        GPS.L1.svn          =   svnt;
        GPS.L1.cn0          =   CN0t;
        GPS.L1.nsat         =   length(prt);
        if strcmp(const,'GPS')
            GPS.L1.eph          =   epht.gpsL1;
        else
            GPS.L1.eph      =   epht.galE1;
        end
        GPS.L1.f            =   1575.42e6;
        %
        pr                  =   GPS.L1.pr;
        svn                 =   GPS.L1.svn;
        cn0                 =   GPS.L1.cn0;
        eph                 =   GPS.L1.eph;
        Nsat                =   GPS.L1.nsat;
        %
        GPS.pr              =   pr;
        GPS.svn              =   svn;
        GPS.cn0              =   cn0;
        GPS.nsat              =   Nsat;
    end
    if flgL5
        [prt,svnt,CN0t]     =   getMeas(measL5);
        GPS.L5.pr           =   prt;
        GPS.L5.svn          =   svnt;
        GPS.L5.cn0          =   CN0t;
        GPS.L5.nsat         =   length(prt);
        if strcmp(const,'GPS')
            GPS.L5.eph      =   epht.gpsL5;
        else
            GPS.L5.eph      =   epht.galE5a;
            fact            =   (1575.42e6/1176.45e6)^2;
        end
        GPS.L5.f            =   1176.45e6;
        %
        pr                  =   GPS.L5.pr;
        svn                 =   GPS.L5.svn;
        cn0                 =   GPS.L5.cn0;
        eph                 =   GPS.L5.eph;
        Nsat                =   GPS.L5.nsat;
        %
        GPS.pr              =   pr;
        GPS.svn              =   svn;
        GPS.cn0              =   cn0;
        GPS.nsat              =   Nsat;
    end
    flg                     =   flgL1 && flgL5;
    %
    if flg % If both L1 and L5 signals selected
        [GPS,sv2]   =   get2freqMeas(GPS);
        N2f         =   length(sv2);
%         flg2        =   acq_info.flags.corrections.f2corr;
        if flg2 % if 2freq iono corrections selected
            % L1 correction
            [iono2freq1,idx]        =   getiono2freqCorr_Dani(GPS.L1,GPS.L5); % Cambiar nombre...  
            GPS.L1.ionoCorr         =   zeros(GPS.L1.nsat,1);
            GPS.L1.ionoCorr(idx)    =   iono2freq1;
            % L2 correction
            [iono2freq2,idx]        =   getiono2freqCorr_Dani(GPS.L5,GPS.L1); % Cambiar nombre...  
            GPS.L5.ionoCorr         =   zeros(GPS.L5.nsat,1);
            GPS.L5.ionoCorr(idx)    =   iono2freq2;
            %
            % Yo aplicaria correcciones a L5 ya que si hay 2freqs es el
            % pseudorango que se utiliza
            ionoCorr                =   zeros(GPS.nsat,1);
            ionoCorr(1:N2f)         =   iono2freq2;
            %
            GPS.ionoCorr2f          =   iono2freq2;
            GPS.svn2f               =   sv2;    
        else
            ionoCorr                =   zeros(GPS.nsat,1);
        end
        pr      =   GPS.pr;
        svn     =   GPS.svn;
        cn0     =   GPS.cn0;
        Nsat    =   GPS.nsat;
        X       =   zeros(3,Nsat);
        tcorr   =   zeros(Nsat,1);
        for ii = 1:Nsat
            if( ii <= GPS.L5.nsat )
                eph     =   GPS.L5.eph;
                fact    =   (1575.42e6/1176.45e6)^2;
            else
                eph     =   GPS.L1.eph;
                fact    =   1;
            end
            [X(:,ii),tau,tgd]   =   getCtrl_corr(eph,svn(ii),TOW,pr(ii),const);
            tcorr(ii)           =   tau - fact*tgd;
        end
        GPS.X           =   X;
        GPS.tcorr       =   tcorr;
        GPS.ionoCorr    =   ionoCorr;
    else % Only L1 or L5 selected
        %
        % Get SatPos and SatClock corrections
        %
%         Nsat            =   length(svn);
        X               =   zeros(3,Nsat);
        tcorr           =   zeros(Nsat,1);
        ionoCorr        =   zeros(Nsat,1);
        for ii = 1:Nsat
            [X(:,ii),tau,tgd]   =   getCtrl_corr(eph,svn(ii),TOW,pr(ii),const);
            tcorr(ii)           =   tau - fact*tgd;
        end
        GPS.X           =   X;
        GPS.tcorr       =   tcorr;
        GPS.ionoCorr    =   ionoCorr;
    end
    %
%     meas.pr         =   pr;
%     meas.svn        =   svn;
%     meas.cn0        =   cn0;
%     corr.ionoCorr   =   ionoCorr;  
%     corr.tcorr      =   tcorr;  






end