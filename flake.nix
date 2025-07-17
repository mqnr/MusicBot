{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    systems.url = "github:nix-systems/default";
  };

  outputs =
    {
      self,
      nixpkgs,
      systems,
      ...
    }:
    let
      inherit (nixpkgs) lib;

      eachSystem = lib.genAttrs (import systems);

      pkgsForEach = eachSystem (system: import nixpkgs { localSystem.system = system; });
    in
    {
      devShells = lib.mapAttrs (system: pkgs: {
        default = pkgs.mkShell {
          packages = [
            pkgs.jdk
            pkgs.maven
          ];
        };
      }) pkgsForEach;
    };
}
