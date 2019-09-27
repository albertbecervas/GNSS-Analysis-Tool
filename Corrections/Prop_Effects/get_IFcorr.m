function pr_if = get_IFcorr(pr,f)
%get_IFcorr Applies ionosphere corrections in pseudoranges.
%
% Input:
%           pr:     1x2 vector defining pseudoranges in concrete
%                   frequencies.
%           f:      1x2 vector defining frequency of pseudoranges
%                   respectively.
%
% Output:
%           pr_if:  1x2 vector defining pseudoranges with corrected
%                   ionosphere.
%

frec_name   =   {'L1', 'L2', 'L2(2)', 'L5'};
frec_value  =   [1575.42 1227.60 0 1176.45];

frec = containers.Map(frec_name,frec_value);

if (f(1,:) == f(2,:))
    warning('The frequencies must be different.');
else
    NaN_pos = find(isnan(pr));
    if (NaN_pos ~= 0) pr(NaN_pos) = 0; end
    
    f1  =   frec(f(1,:));
    f2  =   frec(f(2,:));
    
    a1 = (f1^2) / ((f1^2) - (f2^2));
    a2 = (f2^2) / ((f1^2) - (f2^2));
    
    pr_if = a1*pr(1) + a2*pr(2); 
end

end

