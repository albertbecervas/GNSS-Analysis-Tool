function [pd,A,tropoCorr,ionoCorr] = getMeasandState(acq_info,X,PVT,iono,ionoCorr,tcorr,pr)

    c               =   299792458;
    Nsat            =   length(pr);
    tropoCorr       =   zeros(Nsat,1);
    pd              =   zeros(Nsat,1);
    A               =   zeros(Nsat,3);
    %
    for sat = 1:Nsat

        % Get Propagation Effects Corrections
        if acq_info.flags.corrections.ionosphere || acq_info.flags.corrections.troposphere
            [tropoC,ionoC]           =   getProp_corr(X(:, sat),PVT,iono,acq_info.tow);  % Iono + Tropo correction
            tropoCorr(sat)  =   tropoC;
        end
        if( ionoCorr(sat) ~= 0 && acq_info.flags.corrections.f2corr )
            ionoC           =   ionoCorr(sat);
        else
            ionoCorr(sat)   =   ionoC;
        end
        % Pseudorange corrections
        flagT               =   acq_info.flags.corrections.troposphere;
        flagI               =   acq_info.flags.corrections.ionosphere;
        correct             =   c*tcorr(sat) - flagT*tropoC - flagI*ionoC;
        %
        pr_c        =   pr(sat) + correct;
        %
        % Geometric matrix generation
        if( ~isnan(pr_c) )
            %--     Fill the measurement vector (rhoc_i - d0_i)
            d0          =   sqrt(sum((X(:,sat) - PVT(1:3)).^2));
            pd(sat)     =   pr_c - d0;
            %
            %--     Fill the geometry matrix
            ax              =   -(X(1, sat) - PVT(1)) / d0;   % dx/d0
            ay              =   -(X(2, sat) - PVT(2)) / d0;   % dy/d0
            az              =   -(X(3, sat) - PVT(3)) / d0;   % dz/d0
            A(sat, 1:3)     =   [ax ay az];
            % 
        end            
    end
end