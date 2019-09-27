function acq_info = apply_mask(acq_info, maskType, maskValue)

    % Elevation mask
    if maskType == 1
        tmp     =   [];
        if acq_info.flags.constellations.GPS
            % L1
            for i=1:length(acq_info.SV.GPS.GPSL1)
                if ~isempty(acq_info.SV.GPS.GPSL1(i).Elevation)
                    if acq_info.SV.GPS.GPSL1(i).Elevation < maskValue
                        tmp                     =   [tmp i]; 
                    end
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.GPS.GPSL1(tmp(i))   	=   [];
            end
            
            % L5
            for i=1:length(acq_info.SV.GPS.GPSL5)
                if ~isempty(acq_info.SV.GPS.GPSL5(i).Elevation)
                    if acq_info.SV.GPS.GPSL5(i).Elevation < maskValue
                        tmp                     =   [tmp i]; 
                    end
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.GPS.GPSL5(tmp(i))   	=   [];
            end
        end

        tmp     =   [];
        if acq_info.flags.constellations.Galileo
            % E1
            for i=1:length(acq_info.SV.Galileo.GalileoE1)
                if ~isempty(acq_info.SV.Galileo.GalileoE1(i).Elevation)
                    if acq_info.SV.Galileo.GalileoE1(i).Elevation < maskValue
                        tmp                     =   [tmp i];
                    end      
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.Galileo.GalileoE1(tmp(i))	=   [];
            end
            
            % E5a
            for i=1:length(acq_info.SV.Galileo.GalileoE5a)
                if ~isempty(acq_info.SV.Galileo.GalileoE5a(i).Elevation)
                    if acq_info.SV.Galileo.GalileoE5a(i).Elevation < maskValue
                        tmp                     =   [tmp i];
                    end       
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.Galileo.GalileoE5a(tmp(i))	=   [];
            end
        end
    end

    % CN0 mask
    if maskType == 2
        tmp     =   [];
        if acq_info.flags.constellations.GPS
            % L1
            for i=1:length(acq_info.SV.GPS.GPSL1)
                if ~isempty(acq_info.SV.GPS.GPSL1(i).CN0)
                    if acq_info.SV.GPS.GPSL1(i).CN0 < maskValue
                        tmp                     =   [tmp i]; 
                    end
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.GPS.GPSL1(tmp(i))   	=   [];
            end
            
            % L5
            tmp     =   [];
            for i=1:length(acq_info.SV.GPS.GPSL5)
                if ~isempty(acq_info.SV.GPS.GPSL5(i).CN0)
                    if acq_info.SV.GPS.GPSL5(i).CN0 < maskValue
                        tmp                     =   [tmp i]; 
                    end
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.GPS.GPSL5(tmp(i))   	=   [];
            end
        end

        tmp     =   [];
        if acq_info.flags.constellations.Galileo
            % E1
            for i=1:length(acq_info.SV.Galileo.GalileoE1)
                if ~isempty(acq_info.SV.Galileo.GalileoE1(i).CN0)
                    if acq_info.SV.Galileo.GalileoE1(i).CN0 < maskValue
                        tmp                     =   [tmp i];
                    end      
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.Galileo.GalileoE1(tmp(i))	=   [];
            end
            
            
            % E5a
            tmp     =   [];
            for i=1:length(acq_info.SV.Galileo.GalileoE5a)
                if ~isempty(acq_info.SV.Galileo.GalileoE5a(i).CN0)
                    if acq_info.SV.Galileo.GalileoE5a(i).CN0 < maskValue
                        tmp                     =   [tmp i];
                    end       
                end
            end
            tmp     = 	sort(tmp, 'descend');
            for i=1:length(tmp)
                acq_info.SV.Galileo.GalileoE5a(tmp(i))	=   [];
            end
        end
    end
end