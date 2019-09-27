% Reading JSON information reported by the phone

function [GNSS_info] = load_info(json_fn)

    fid         =   fopen(json_fn);
    aux         =   fread(fid, inf);
    GNSS_info   =   char(aux');
    fclose(fid);
    
end