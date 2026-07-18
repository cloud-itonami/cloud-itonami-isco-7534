# Security Policy

This project handles upholstery workshop operating workflows. Treat
vulnerabilities as potentially high impact even when the demo data is
synthetic — this domain's failure modes include physical worker-safety
risk from hand-tool injury (staple guns, tack hammers, cutting knives)
and padding/foam material-handling hazards.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real upholsterer, workshop or operator data exposure
- authorization bypass
- UpholCoordGovernor bypass
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe robot action dispatch
- any path that lets a proposal reach an upholstery-execution
  decision, a workshop-safety-clearance decision, or a
  shop-safety-officer-override decision

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on upholsterer/workshop data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real upholsterer/workshop/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
