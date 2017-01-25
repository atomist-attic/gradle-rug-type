import {TreeNode} from '@atomist/rug/tree/PathExpression'

export interface Gradle extends TreeNode {

  encrypt(repo: string, token: string, org: string, content: string): void

  enable(repo: string, token: string, org: string): void

  disable(repo: string, token: string, org: string): void
}
