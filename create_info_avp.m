function acq_info = create_info_avp(acq_infos)

    acq_info    =   acq_infos(end);
    
    for i=1:length(acq_infos)-1
        % GPS
        for j=1:length(acq_infos(i).SV.GPS)
            for z=1:length(acq_infos(i+1).SV.GPS)
                if (acq_infos(i).SV.GPS(j).svid == acq_infos(z).SV.GPS(j).svid) && (acq_infos(j).SV.GPS(j).constellationType == acq_infos(z).SV.GPS(j).constellationType)
                    acq_info.SV.GPS(j).p = acq_info.SV.GPS(j).p + acq_infos(i+1).SV.GPS(j).p;
                end
            end
        end
        
        % Galileo
        for j=1:length(acq_infos(i).SV.Galileo)
            acq_info.SV.Galileo(j).p = acq_info.SV.Galileo(j).p + acq_infos(i+1).SV.Galileo(j).p;
        end     
    end
    
    % GPS pseudorange mean
    for i=1:length(acq_info.SV.GPS)
       acq_info.SV.GPS(i).p =  acq_info.SV.GPS(i).p/length(acq_infos);
    end
    
    % Galileo pseudorange mean
    for i=1:length(acq_info.SV.Galileo)
       acq_info.SV.Galileo(i).p =  acq_info.SV.Galileo(i).p/length(acq_infos);
    end
    
end