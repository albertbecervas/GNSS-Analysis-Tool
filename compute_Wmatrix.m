function W = compute_Wmatrix(eL,CN0,type)
% Thanks to Vicente Lucas (UAB-SPCOMNAV)!!!

        switch(type)
            case 1
                w   =   1./sin(eL * pi/180).^2;                        % Sinusoidal weighting method [Rahemi, N., et al. "Accurate solution of navigation equations in GPS receivers for very high velocities using pseudorange measurements."]
            case 2
                w   =   1./tan(eL * pi/180-0.1).^2;                    % Tangential weighting method [Rahemi, N., et al. "Accurate solution of navigation equations in GPS receivers for very high velocities using pseudorange measurements."]
            case 3
                w   =   0.244*10.^(-0.1*CN0);                         % C/N0 weighting method - Sigma e [Wieser, Andreas, et al. "An extended weight model for GPS phase observations"]
            case 4
                w   =   1*(10.^(-0.1*CN0))./sin(eL * pi/180).^2;    % C/N0+sinusoidal weighting method [Tay, Sarab, et al. "Weighting models for GPS Pseudorange observations for land transportation in urban canyons"]
            case 5
                w   =   1*(10.^(-0.1*CN0))./tan(eL * pi/180-0.1).^2;% C/N0+tangential weighting method [Inspired by Satab Tay, et al. paper]
            otherwise
                error('The selected weighting method is not available')
        end

    % Weight is CN0
%     weight  =	10.^(weight/10)/sum(10.^(weight/10));
    W       =   diag(1./w);
    
end