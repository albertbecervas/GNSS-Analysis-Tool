function pseudorange = pseudo_gen(t_tx, t_rx, c)

pseudorange = ((t_rx - t_tx)/1e9)*c;
end